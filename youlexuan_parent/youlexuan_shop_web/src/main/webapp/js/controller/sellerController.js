//seller控制层 
app.controller('sellerController' ,function($scope, $controller, sellerService){	
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	
	// 注册
	$scope.add = function () {
		sellerService.add($scope.entity).success(function (resp) {
			if (resp) {
				location.href = 'shoplogin.html';
			} else {
				alert(resp.message);
			}
		})
	}
    
});	
