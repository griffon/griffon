<%include "header.gsp"%>
<%include "menu.gsp"%>

      <div class="row-fluid marketing">
        <div class="span9">
          <h2>News</h2>
            <table class="tableblock frame-all grid-all" ">
                <colgroup>
                    <col style="width:30%;">
                    <col style="width:50%;">
                </colgroup>
                <tbody>
                    <%posts.each {post ->%>
                         <%if (post.status == "published" && post.category == "news") {%>
                    <tr>
                        <td class="tableblock halign-left valign-top"><p class="tableblock">${post.date.format("dd MMM yyyy")}</p></td>
                        <td class="tableblock halign-left valign-top"><p class="tableblock"><a href="/${post.uri}">${post.title}</a></p></td>
                    </tr>
                         <%}%>
                    <%}%>
                </tbody>
            </table>
          <p></p>
        </div>
      </div>

      <hr>

<%include "footer.gsp"%>