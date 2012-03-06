<%@ page contentType="text/xml;charset=UTF-8" language="java" %><?xml version='1.0' encoding='UTF-8'?>
<kml xmlns='http://www.opengis.net/kml/2.2'>
  <NetworkLinkControl>
    <Update>
      <targetHref><%=com.google.appengine.arsenal.Mappings.baseURLnwlink%>/Gros.kmz</targetHref>
      <Change>
      
		    <Placemark targetId="virtualPlacemark"> 
	        <visibility>0</visibility>
	      </Placemark>
<%
int x = ((Integer)request.getAttribute("x")).intValue();
int y = ((Integer)request.getAttribute("y")).intValue();
if (x >= 0 && x <= 5 && y >=0 && y <= 7) {
  int marker_id = ((Integer)request.getAttribute("marker_id")).intValue();
  //if image is full resolution 90Mpx : use : 1920 & 1080 for overlayXY and size 1&1 
  //if image is full HD : use : 320 & 135 for overlayXY, and size : 6&8
%>
        <ScreenOverlay targetId="ballonOverlay">
          <visibility>1</visibility>
          <name>Absolute Positioning: Top</name>
          <Icon>
            <href>http://lg-head:8080/static/logoarsenal<%=marker_id%>.png</href>
          </Icon>
          <overlayXY x="<%=(5-x)*320 %>" y="<%=y*135%>" xunits="pixels" yunits="pixels"/>
          <screenXY x="0" y="0" xunits="pixels" yunits="pixels"/>
          <size x="6" y="8" xunits="fraction" yunits="fraction"/>
        </ScreenOverlay>
<% } else { %>
        <ScreenOverlay targetId="ballonOverlay">
          <visibility>0</visibility>
          <name>hidden</name>
        </ScreenOverlay>
<% } %>			
	    </Change>
	  </Update>
  </NetworkLinkControl>
</kml>
