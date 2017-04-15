<html>
<body>

<h2>Hello World!</h2>

<form enctype="multipart/form-data" action="./rest/evaluate" method="post">
    <input type="file" required multiple name="uploadedFiles"/>
    <input type="submit" value="Send">
</form>
</body>
<script src="./js/jquery-3.2.1.js"></script>
</html>
