<!doctype html>
<html lang="en" ng-app="ruleApp">
	<head>
		<meta charset="utf-8">
		<title>Implementer's Workbench</title>
		<meta name="description" content="Login screen">
		<meta name="author" content="Edinardo Potrich">
		<link rel="stylesheet" href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.no-icons.min.css">
		<link rel="stylesheet" href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css">
		<link rel="stylesheet" href="lib/ng-grid/ng-grid.min.css">
		<link rel="stylesheet" href="css/app.css"/>
	</head>
	<body>
	<header>
		<a  href="#/home"><i class="icon-edit-sign icon-5x"></i></a>
		<div class="mainMenu">
			<h2 ng-bind-html-unsafe="title"></h2>
			<ul class="nav nav-pills">
				<li ng-repeat="menuItem in menuItems" ng-class="menuItem.status"><a href="{{menuItem.href}}">{{menuItem.text}}</a></li>
			</ul>
		</div>
		<div>
			Welcome, <a href="" style="text-transform: capitalize;"><%=request.getRemoteUser()%></a><br>
			<a href="logout">Log Out</a>
		</div>
	</header>

	<ng-view class="container-fluid"></ng-view>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
	<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.1.5/angular.min.js"></script>
	<script src="//tinymce.cachefly.net/4.0/tinymce.min.js"></script>
	<script src="http://d3js.org/d3.v3.min.js"></script>
	<script src="lib/ng-grid/ng-grid-2.0.7.min.js"></script>
	<script src="lib/blockly/blockly_compressed.js"></script>
	<script src="lib/blockly/msg/js/en_us.js"></script>
	<script src="partials/standard/expression/operators.js"></script>
    <script src="lib/ui-bootstrap/ui-bootstrap-tpls-0.6.0.min.js"></script>
	<script src="lib/ui-tinymce/tinymce.js"></script>
	<script src="js/app.js"></script>
	<script src="js/controllers.js"></script>
	</body>
</html>