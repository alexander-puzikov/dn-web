<html>
<body>

<h2>Hello World!</h2>

<form id="sendFileForm" enctype="multipart/form-data" method="post">
    <input type="file" required multiple name="uploadedFiles"/>
    <input type="submit" value="Send"/>
</form>
</body>
<script type="application/javascript" src="./js/jquery-3.2.1.js"></script>
<script type="text/javascript">
    $('#sendFileForm').submit(function (e) {
            var formData = new FormData($(this));
            $.ajax({
                async: false,
                url: './rest/evaluate',
                data: formData,
                method: 'POST',
                success: onSuccess(data, status),
                error: function (jqXHR, textStatus, errorThrown) {

                }
            });
            e.preventDefault();
            e.unbind();
        }
    );
    function onSuccess(data, status) {
        console.log(data)
    }
</script>
</html>
