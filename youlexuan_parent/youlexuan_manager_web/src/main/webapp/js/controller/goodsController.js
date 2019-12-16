//goods控制层 
app.controller('goodsController' ,function($scope, $controller, goodsService, itemCatService){
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	
	// 保存
	$scope.save = function() {
		goodsService.save($scope.entity).success(function(response) {
			if (response.success) {
				// 重新加载
				$scope.reloadList();
			} else {
				alert(response.message);
			}
		});
	}
	
	//查询实体 
	$scope.findOne = function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//批量删除 
	$scope.dele = function(){			
		//获取选中的复选框			
		goodsService.dele($scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.reloadList();
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	// 定义搜索对象 
	$scope.searchEntity = {auditStatus:'0'};
	// 搜索
	$scope.search = function(page,size){			
		goodsService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}

	// 定义状态数组
	$scope.goodsStatus = ['未审核', '审核通过', '审核未通过', '关闭'];

	// 查询全部itemCat
	$scope.itemCatList = [];
	$scope.findItemCatList = function () {
		itemCatService.findItemCatList().success(function (resp) {
			if (resp) {
				for (var i = 0; i < resp.length; i++) {
					$scope.itemCatList[resp[i].id] = resp[i].name;
				}
			}
		})
	}

	// 修改商品状态
	$scope.updateAuditState = function (status) {
		goodsService.updateAuditState($scope.selectIds, status).success(function (resp) {
			if (resp.success) {
				$scope.reloadList();
				$scope.selectIds = [];
			} else {
				alert(resp.message);
			}
		})
	}
    
});	
