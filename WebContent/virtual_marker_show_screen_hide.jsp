<%@ page contentType="text/xml;charset=UTF-8" language="java" %><?xml version='1.0' encoding='UTF-8'?>
<kml xmlns='http://www.opengis.net/kml/2.2'>
  <NetworkLinkControl>
    <Update>
      <targetHref><%=com.google.appengine.arsenal.Mappings.baseURLnwlink%>/Gros.kmz</targetHref>
      <Change>
      
		    <Placemark targetId="virtualPlacemark">
          <name><%=request.getAttribute("formatted_address")%></name>
          <visibility>1</visibility>
          <Point>
            <extrude>1</extrude>
            <altitudeMode>relativeToGround</altitudeMode>
            <coordinates><%=request.getAttribute("lng")%>,<%=request.getAttribute("lat")%>,100</coordinates>
          </Point>
        </Placemark>
<%
int x = ((Integer)request.getAttribute("x")).intValue();
int y = ((Integer)request.getAttribute("y")).intValue();
if (x == 0 && y == 0) { %>
			  <ScreenOverlay targetId="ballonOverlay">
			    <visibility>1</visibility>
			    <name>Absolute Positioning: Top</name>
			    <Icon>
			      <href>http://lg-head/style/src/googlewhite.png</href>
			    </Icon>
			    <overlayXY x="0" y="0" xunits="pixels" yunits="pixels"/>
			    <screenXY x="800" y="0" xunits="pixels" yunits="pixels"/>
			    <size x="-1" y="-1" xunits="fraction" yunits="fraction"/>
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
