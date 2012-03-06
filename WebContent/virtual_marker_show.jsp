<%@ page contentType="text/xml;charset=UTF-8" language="java" %><?xml version='1.0' encoding='UTF-8'?>
<kml xmlns='http://www.opengis.net/kml/2.2'>
  <NetworkLinkControl>
    <Update>
      <targetHref><%=com.google.appengine.arsenal.Mappings.baseURLnwlink%>/Podium.kmz</targetHref>
      <Change>
      
        <Placemark targetId="virtualPlacemark">
	        <name><%=request.getAttribute("formatted_address")%></name>
	        <visibility>1</visibility>
	        <Point>
	          <extrude>1</extrude>
	          <altitudeMode>relativeToGround</altitudeMode>
	          <coordinates><%=request.getAttribute("lng")%>,<%=request.getAttribute("lat")%>,40</coordinates>
	        </Point>
	      </Placemark>
	      
	    </Change>
	  </Update>
  </NetworkLinkControl>
</kml>
