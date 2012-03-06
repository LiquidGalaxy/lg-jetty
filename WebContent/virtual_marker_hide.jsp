<%@ page contentType="text/xml;charset=UTF-8" language="java" %><?xml version='1.0' encoding='UTF-8'?>
<kml xmlns='http://www.opengis.net/kml/2.2'>
  <NetworkLinkControl>
    <Update>
      <targetHref><%=com.google.appengine.arsenal.Mappings.baseURLnwlink%>/Podium.kmz</targetHref>
      <Change>
      
		    <Placemark targetId="virtualPlacemark"> 
	        <visibility>0</visibility>
	      </Placemark>
	      
	    </Change>
	  </Update>
  </NetworkLinkControl>
</kml>
