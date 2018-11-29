package Ex03.Servlets;

import Ex03.Manager.ChatManager;
import Ex03.Manager.SingleGameManager;
import Ex03.Utils.ServletUtils;
import Ex03.Utils.SingleChatEntry;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChatServlet extends HttpServlet {

    // Returns a list of the room chat entries
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        SingleGameManager userGame = ServletUtils.GetGamesManager(request.getServletContext())
                .GetGameBySession(request.getSession());

        try (PrintWriter out = response.getWriter()) {
            List<SingleChatEntry> chatEntries = chatManager.getChatEntries(0, userGame.GetGameName());
            Chat cav = new Chat(chatEntries);
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(cav);
            out.print(jsonResponse);
            out.flush();
        }
    }

    // Nested class that holds the chat entries of a chat
    class Chat {

        final private List<SingleChatEntry> entries;
        public Chat(List<SingleChatEntry> entries) {
            this.entries = entries;
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Chat servlet";
    }// </editor-fold>
}
