<%include "header.gsp"%>

    <%include "menu.gsp"%>

      <div class="row-fluid marketing">
        <div class="span9">
          <h2>News</h2>
          <ul>
              <%posts.each {post ->%>
                  <%if (post.status == "published" && post.category == "news") {%>
                      <li>${post.date.format("dd MMMM yyyy")} - <a href="/${post.uri}">${post.title}</a></li>
                  <%}%>
              <%}%>
          </ul>
          <p></p>
        </div>
      </div>

      <hr>

<%include "footer.gsp"%>