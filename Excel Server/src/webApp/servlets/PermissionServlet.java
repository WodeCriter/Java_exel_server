package webApp.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.perms.PermissionRequest;
import webApp.utils.ServletUtils;

import java.io.IOException;

import static utils.Constants.GSON_INSTANCE;

@WebServlet("/files/permissions") //todo: add to constant
public class PermissionServlet extends HttpServlet
{
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        PermissionRequest permRequest = GSON_INSTANCE.fromJson(request.getParameter("permissionRequest"),
                PermissionRequest.class);
        boolean toApprove = Boolean.parseBoolean(request.getParameter("toApprove"));

        ServletUtils.getRequestManager(getServletContext()).approveOrDenyRequest(permRequest, toApprove);
        HomeServlet.increaseRequestNumber();
    }
}
