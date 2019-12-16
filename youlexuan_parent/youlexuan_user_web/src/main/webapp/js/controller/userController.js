//user控制层 
app.controller('userController' ,function($scope, userService){

	$scope.entity = {username:'',password:'',phone:''};

	// 注册
	$scope.save = function() {

		if ($scope.entity.password != $scope.repassword) {
			alert("两次密码不一致, 请重新输入");
			return;
		}

		userService.save($scope.entity, $scope.smsCode).success(function(response) {

			if (response.success) {
				location.href = "http://localhost:9005/login.html"
			} else {
				alert(response.message);
			}

		});
	}

	// 发送短信验证码
	$scope.sendCode = function () {
		if ($scope.entity.phone == '') {
			alert("请输入手机号");
			return;
		}

		userService.sendCode($scope.entity.phone).success(function (resp) {
			if (resp.success) {

				$('#codeBtn').attr("disabled", "disabled");

				var i = 10;
				var timer = setInterval(function () {

					if (i <= 1) {
						$('#codeBtn').removeAttr("disabled");
						$('#codeBtn').html('获取短信验证码');
						clearInterval(timer);
					}else {
						$('#codeBtn').html(--i + '秒后重发')
					}

				}, '1000');

			} else {
				alert(resp.message);
			}
		})
	}
    
});	
