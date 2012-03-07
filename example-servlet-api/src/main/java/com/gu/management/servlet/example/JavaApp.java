package com.gu.management.servlet.example;

import com.gu.management.Switch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Callable;

public class JavaApp extends HttpServlet {
    private Switch shouldBeDown = Switches.takeItDown();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        TimingMetrics.requests().call(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if (shouldBeDown.isSwitchedOn()) {
                    response.sendError(500, "Temporarily switched off!");
                } else {
                    response.getWriter().println("Thank you for invoking this app!");
                }

                return null;
            }
        });


    }
}
