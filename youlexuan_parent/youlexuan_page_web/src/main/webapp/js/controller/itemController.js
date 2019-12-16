//商品详细页（控制层）
app.controller('itemController', function($scope) {
	
	
	$scope.num = 1;
	
	$scope.changeNum = function(number) {
		
		$scope.num = $scope.num + number;
		
		if($scope.num < 1) {
			$scope.num = 1;
		}
	
	}
	
	$scope.specificationItems={};//记录用户选择的规格
	// 用户选择规格
	$scope.updateSpec = function(specName, specVal) {
		$scope.specificationItems[specName] = specVal;
		
		// 更改进入页面的默认sku变量
		changeSku();
	}
	
	// 判断某个规格选项是否选中
	$scope.isSelected = function(specName, specVal) {
		
		if($scope.specificationItems[specName] != specVal) {
			return false;
		}else {
			return true;
		}
		
	}
	
	//如果某些规格选项组合起来没有商品，给出一些提示字样
	$scope.sku={id:0,title:'--------',price:0};
	
	// 加载默认的sku
	$scope.loadSku = function() {
		$scope.sku = itemList[0];
		
		// 默认用户选中
		$scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
	}
		
		
	
	// 更改进入页面的默认sku变量
	changeSku = function() {
		for (var i = 0; i < itemList.length; i++) {
			
			if(equalObj(itemList[i].spec, $scope.specificationItems)) {
				
				$scope.sku = itemList[i];
				return;
				
			}
			
		}
	}
	
	// 匹配两个对象
	equalObj = function(obj1, obj2) {
		for (var k in obj1) {
			if(obj1[k] != obj2[k]) {
				return false;
			}
		}
		
		for (var k in obj2) {
			if(obj2[k] != obj1[k]) {
				return false;
			}
		}
		
		return true;
	}
	
	// 加入购物车
	$scope.addCart = function() {
		alert($scope.sku.id, $scope.num);
	}
	
})