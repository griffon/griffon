<% import static groovy.xml.XmlUtil.escapeXml %><?xml version="1.0"?>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
  <channel>
    <title>Griffon</title>
    <link>${config.site_host}</link>
    <atom:link href="${config.site_host}/${config.feed_file}" rel="self" type="application/rss+xml" />
    <description>Griffon</description>
    <language>en-gb</language>
    <pubDate>${published_date.format("EEE, d MMM yyyy HH:mm:ss Z")}</pubDate>
    <lastBuildDate>${published_date.format("EEE, d MMM yyyy HH:mm:ss Z")}</lastBuildDate>

    <%published_posts.each {post -> %>
    <item>
      <title>${post.title}</title>
      <link>${config.site_host}/${post.uri}</link>
      <pubDate>${post.date.format("EEE, d MMM yyyy HH:mm:ss Z")}</pubDate>
      <guid isPermaLink="false">${post.uri}</guid>
      <description>
      ${escapeXml(post.body)}
      </description>
    </item>
    <%}%>

  </channel> 
</rss>
