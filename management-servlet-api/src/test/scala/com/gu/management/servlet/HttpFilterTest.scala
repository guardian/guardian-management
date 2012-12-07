package com.gu.management.servlet

import com.gu.management._
import javax.servlet.http.{ HttpServletRequest, HttpServletResponse, HttpServletRequestWrapper }
import javax.servlet.{ ServletResponse, ServletRequest, FilterChain }
import org.scalatest
import scalatest.FlatSpec
import scalatest.matchers.ShouldMatchers
import scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import java.util
import net.liftweb.mocks.MockHttpServletRequest
import com.gu.management.HttpRequest
import servlet.ServletHttpResponse

class FakeManagementPage(override val path: String, val response: Response, override val needsAuth: Boolean) extends ManagementPage {
  override def dispatch = { case _ => response }
  override def canDispatch(request: HttpRequest) = true
  override def get(req: HttpRequest): Response = response
}

class HttpFilterTest extends FlatSpec with ShouldMatchers with MockitoSugar {
  trait FilterWithPageTest {
    val mockRequest = new MockHttpServletRequest("/foo")
    val mockResponse = mock[Response]
    val mockHttpResponse = mock[HttpServletResponse]
    val finalChain = mock[FilterChain]
    val mockUserProvider = mock[UserProvider]
    def pageUnderTest: ManagementPage
    val testFilter = new ManagementFilter {
      val userProvider = mockUserProvider
      val applicationName = "foo"
      val pages = pageUnderTest :: Nil
    }
    when(mockUserProvider.realm) thenReturn "test"
  }

  "ManagementFilter" should "pass onto page if path matches" in {
    new FilterWithPageTest {
      // Given a page that does not require authentication
      override def pageUnderTest = new FakeManagementPage("/foo", mockResponse, false)

      // When we call doHttpFilter
      testFilter.doHttpFilter(mockRequest, mockHttpResponse, finalChain)
      // It should call the page to render to the response
      verify(mockResponse).sendTo(any[ServletHttpResponse]())
      // It should not call the chain further
      verifyNoMoreInteractions(finalChain)
      verifyNoMoreInteractions(mockResponse)
    }
  }

  it should "return a 401 Unauthorised for unauthorised access of the path" in {
    new FilterWithPageTest {
      // Given a page that requires authorization
      override def pageUnderTest = new FakeManagementPage("/foo", mockResponse, true)
      // When we call doHttpFilter
      testFilter.doHttpFilter(mockRequest, mockHttpResponse, finalChain)
      // It should call the page to render to the response
      verify(mockHttpResponse).sendError(401, "Needs Authorisation")
      verify(mockHttpResponse).addHeader("WWW-Authenticate", "Basic realm=\"test\"")
      // It should not call the chain further
      verifyNoMoreInteractions(finalChain)
      verifyNoMoreInteractions(mockResponse)
    }
  }

  it should "accept an Authorization header" in {
    new FilterWithPageTest {
      // Given a page that requires authorization
      override def pageUnderTest = new FakeManagementPage("/foo", mockResponse, true)
      // And a user provider for the username
      when(mockUserProvider.isValid(UserCredentials("user", "pass"))) thenReturn true
      // And a request with an authorization header
      mockRequest.addBasicAuth("user", "pass")

      // When we call doHttpFilter
      testFilter.doHttpFilter(mockRequest, mockHttpResponse, finalChain)
      // It should check with the userProvider
      verify(mockUserProvider).isValid(UserCredentials("user", "pass"))
      // It should call the page to render to the response
      verify(mockResponse).sendTo(any[ServletHttpResponse]())
      // It should not call the chain further
      verifyNoMoreInteractions(finalChain)
      verifyNoMoreInteractions(mockResponse)
      verifyNoMoreInteractions(mockUserProvider)
    }
  }

  it should "fail the wrong password" in {
    new FilterWithPageTest {
      // Given a page that requires authorization
      override def pageUnderTest = new FakeManagementPage("/foo", mockResponse, true)
      // And a user provider for the username
      when(mockUserProvider.isValid(UserCredentials("user", "pass"))) thenReturn false
      // And a request with an authorization header
      mockRequest.addBasicAuth("user", "pass")

      // When we call doHttpFilter
      testFilter.doHttpFilter(mockRequest, mockHttpResponse, finalChain)
      // It should check with the userProvider
      verify(mockUserProvider).isValid(UserCredentials("user", "pass"))
      // It should send an error back to the user
      verify(mockHttpResponse).sendError(401, "Needs Authorisation")
      verify(mockHttpResponse).addHeader("WWW-Authenticate", "Basic realm=\"test\"")
      // It should not call the chain further
      verifyNoMoreInteractions(finalChain)
      verifyNoMoreInteractions(mockResponse)
    }
  }
}
