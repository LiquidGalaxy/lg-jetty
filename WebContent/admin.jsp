<%@page import="com.google.appengine.arsenal.HeartBeats"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin - Pavilion d'Arsenal</title>
</head>
<body>
<input type="button" value="Podium 4" style="color: black" onclick="document.forms['myform'].servername.value=this.alt" alt="lg-p4">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" value="Podium 3" style="color: black" onclick="document.forms['myform'].servername.value=this.alt" alt="lg-p3">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" value="Podium 2" style="color: black" onclick="document.forms['myform'].servername.value=this.alt" alt="lg-p2">
<br/>
<img src="/static/pavillon-instances.png" height="300px" width="420px" usemap="#hostsmap">
<map name="hostsmap">
  <area shape="rect" coords="0,0,140,75" onclick="document.forms['myform'].servername.value=this.alt" alt="lg7-y7" />
  <area shape="rect" coords="140,0,280,75" onclick="document.forms['myform'].servername.value=this.alt" alt="lg1-y7" />
  <area shape="rect" coords="280,0,420,75" onclick="document.forms['myform'].servername.value=this.alt" alt="lg3-y7" />
  
  <area shape="rect" coords="0,75,140,150" onclick="document.forms['myform'].servername.value=this.alt" alt="lg7-y8" />
  <area shape="rect" coords="140,75,280,150" onclick="document.forms['myform'].servername.value=this.alt" alt="lg1-y8" />
  <area shape="rect" coords="280,75,420,150" onclick="document.forms['myform'].servername.value=this.alt" alt="lg3-y8" />
  
  <area shape="rect" coords="0,150,140,225" onclick="document.forms['myform'].servername.value=this.alt" alt="lg7-y1" />
  <area shape="rect" coords="140,150,280,225" onclick="document.forms['myform'].servername.value=this.alt" alt="lg1-y1" />
  <area shape="rect" coords="280,150,420,225" onclick="document.forms['myform'].servername.value=this.alt" alt="lg3-y1" />
  
  <area shape="rect" coords="0,225,140,300" onclick="document.forms['myform'].servername.value=this.alt" alt="lg7-y2" />
  <area shape="rect" coords="140,225,280,300" onclick="document.forms['myform'].servername.value=this.alt" alt="lg1-y2" />
  <area shape="rect" coords="280,225,420,300" onclick="document.forms['myform'].servername.value=this.alt" alt="lg3-y2" />
</map><br><br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" value="Podium 1" style="color: black" onclick="document.forms['myform'].servername.value=this.alt" alt="lg-p1">

<form target="_self" name="myform">
  Restart on Hostname <input name="servername" /><br/>
  <input type="hidden" name="gui" value="full">
  <input type="submit" name="action" value="1 low mem Earth" style="color: green">
  <input type="submit" name="action" value="2 Squid" style="color: green">
  <input type="submit" name="action" value="3 kill Earth" style="color: blue">
  <input type="submit" name="action" value="4 relaunch Xorg" style="color: blue">
  <input type="submit" name="action" value="5 reboot" style="color: red">
  <input type="submit" name="action" value="6 squid+X" style="color: fuchsia">
</form>
<table border="1">
	<tr>
	   <th colspan="6">TTL in s, reboot credits left</th>
	</tr>
<%
HeartBeats heartBeats = (HeartBeats) application.getAttribute("HB");
if (heartBeats == null) {
  //initialize
  heartBeats = new HeartBeats(HeartBeats.wait); // wait 60s
  //set
  application.setAttribute("HB", heartBeats);
}
long timestamp = System.currentTimeMillis();
for (int y=7;y>=0;y--) {
%><tr><%
  for (int x=5;x>=0;x--) {
%>  <td><%=(heartBeats.hb[x][y] - timestamp)/1000%>,<%=heartBeats.rebootCountDown[x][y] %></td><%
  }
%></tr><%
}
%>
</table>
<img src="/static/pavillon-instances-xy.png" height="250px" width="470px">
</body>
</html>