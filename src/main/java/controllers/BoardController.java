package controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import constants.Constants; //pagination에 사용 될 상수 저장용
import dao.BoardDAO;
import dto.BoardDTO;

@WebServlet("*.board")
public class BoardController extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8"); // 한글깨짐방지
		response.setContentType("text/html;charset=utf8"); // 한글깨짐방지
		
		String cmd = request.getRequestURI();
		System.out.println("board cmd: " + cmd);

		BoardDAO dao = BoardDAO.getInstance();
		PrintWriter pw = response.getWriter();
		Gson gson = new Gson();

		try {
			if (cmd.equals("/insert.board")) {
				// 게시글 등록
				
				
			} else if (cmd.equals("/load.board")) {
				// cpage 가져와야하고,
				// 게시글 번호를 가져와야함.
				// 그리고 게시판 위치 (자유게시판인지 qna인지) -> 이거는 여기서 보내주는 것

				
				// <a href="/load.board?cpage=${cpage }&seq=${post.seq }&category=${category }">
				int cpage =  Integer.parseInt(request.getParameter("cpage"));
				int postSeq = Integer.parseInt(request.getParameter("seq"));
				String category = request.getParameter("category");
				
				BoardDTO post = dao.selectPost(postSeq);
				
				boolean postRec = dao.checkPostRecommend(postSeq, (String)request.getSession().getAttribute("loginID"));
				boolean bookmark = dao.checkPostBookmark(postSeq, (String)request.getSession().getAttribute("loginID"));
				request.setAttribute("post", post);
				request.setAttribute("cpage", cpage);
				request.setAttribute("category", category);
				if(postRec)
					request.setAttribute("postRec",postRec);
				if(bookmark)
					request.setAttribute("bookmark",bookmark);
				request.getRequestDispatcher("/board/post.jsp").forward(request, response);

			} else if (cmd.equals("/update.board")) {
				// 게시글 수정

			} else if (cmd.equals("/delete.board")) {
				// 게시글 삭제

			} else if (cmd.equals("/listing.board")) {
				// 게시판 출력
				String category = request.getParameter("category");
				category = (category == null) ? "rhythm" : category;

				String cpage = request.getParameter("cpage");
				int currentPage = (cpage == null) ? 1 : Integer.parseInt(cpage);
				request.getSession().setAttribute("lastPageNum", currentPage);

				List<BoardDTO> list = new ArrayList<>();

				// 검색한 카테고리, 키워드에 맞는 페이지 찾기
				String search = request.getParameter("search");
				String keyword = request.getParameter("keyword");

				if (keyword == null || keyword.equals("")) {
					// 검색 키워드가 넘어오지 않은 경우
					list = dao.selectByCategory(category,
							currentPage * Constants.RECORD_COUNT_PER_PAGE - Constants.RECORD_COUNT_PER_PAGE,
							Constants.RECORD_COUNT_PER_PAGE);
					request.setAttribute("recordTotalCount", dao.getRecordCount(category));

				} else {
					// 검색 키워드가 넘어온 경우
				}

				List<BoardDTO> notiList = new ArrayList<>();
				notiList = dao.selectByNoti();
				
				request.setAttribute("cpage", cpage);

				request.setAttribute("category", category);
				request.setAttribute("type", "freeBoard");
				request.setAttribute("notiList", notiList);
				request.setAttribute("boardList", list);
				request.setAttribute("recordCountPerPage", Constants.RECORD_COUNT_PER_PAGE);
				request.setAttribute("naviCountPerPage", Constants.NAVI_COUNT_PER_PAGE);
				request.getRequestDispatcher("/board/boardList.jsp").forward(request, response);

			}else if(cmd.equals("/write.board")){
				// 자유게시판에서 글쓰기 누를 때 
				String menu = request.getParameter("menu");
				System.out.println("free "+menu);
				request.setAttribute("menu", menu);
				request.getRequestDispatcher("/qna/qnaWrite.jsp").forward(request, response);
			} else if(cmd.equals("/insertRecommend.board")) {
				int postSeq = Integer.parseInt(request.getParameter("postSeq"));
				int result = dao.insertPostRecommend(postSeq, (String) request.getSession().getAttribute("loginID"));
				System.out.println(result);
				pw.append(gson.toJson(result));
			} else if(cmd.equals("/deleteRecommend.board")) {
				int postSeq = Integer.parseInt(request.getParameter("postSeq"));
				int result = dao.deletePostRecommend(postSeq, (String) request.getSession().getAttribute("loginID"));
				System.out.println(result);
				pw.append(gson.toJson(result));
			} else if(cmd.equals("/insertBookmark.board")) {
				int postSeq = Integer.parseInt(request.getParameter("postSeq"));
				int result = dao.insertPostBookmark(postSeq, (String) request.getSession().getAttribute("loginID"));
				System.out.println(result);
				pw.append(gson.toJson(result));
			} else if(cmd.equals("/deleteBookmark.board")) {
				int postSeq = Integer.parseInt(request.getParameter("postSeq"));
				int result = dao.deletePostBookmark(postSeq, (String) request.getSession().getAttribute("loginID"));
				System.out.println(result);
				pw.append(gson.toJson(result));
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("/error.html");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
