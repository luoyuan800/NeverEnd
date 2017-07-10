<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>挂机玩家列表</title>
    <link rel="stylesheet" type="text/css" href="jquery-easyui-1.5.1/themes/metro/easyui.css">
    <link rel="stylesheet" type="text/css" href="jquery-easyui-1.5.1/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="jquery-easyui-1.5.1/project.css">
    <script type="text/javascript" src="jquery-easyui-1.5.1/jquery.min.js"></script>
    <script type="text/javascript" src="jquery-easyui-1.5.1/jquery.easyui.min.js"></script>
    <%@ page language="java" import="cn.luo.yuan.maze.server.MainProcess" pageEncoding="utf-8"%>
</head>
<body>
<table id="list" title="Users" class="easyui-treegrid" style="height:300px">
	<thead><tr>
		<th>Name</th>
		<th>Id</th>
		<th>restoreLimit</th>
		<th>dieCount</th>
	</tr></thead>
	<tbody>
	<% 
	MainProcess process = MainProcess.process;
	for(cn.luo.yuan.maze.model.ServerRecord record : process.queryRecords(0, -2, "")){
		
	%>
	<tr>
		<th> <%=record.getData().getHero().getDisplayName() %></th>
		<th><%=record.getId() %></th>
		<th><%=record.getRestoreLimit() %></th>
		<th><%=record.getDieCount() %></th>
		<th><a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="/app/add_restore_Limit?id=<%=record.getId()%>&r_l_c=100">增加重生次数</a></th>
	</tr>
	<%} %>
	</tbody>
</table>
</body>
</html>