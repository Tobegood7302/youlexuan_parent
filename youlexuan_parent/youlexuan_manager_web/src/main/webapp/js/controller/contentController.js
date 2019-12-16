//content控制层 
app.controller('contentController' ,function($scope, $controller, contentService, contentCategoryService, uploadService){
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	
	// 保存
	$scope.save = function() {
		if ($scope.entity.status == null) {
			$scope.entity.status = '0';
		}
		contentService.save($scope.entity).success(function(response) {
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
		contentService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//批量删除 
	$scope.dele = function(){			
		//获取选中的复选框			
		contentService.dele($scope.selectIds).success(
			function(response){
				if(response.success){
					$scope.reloadList();
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	// 定义搜索对象 
	$scope.searchEntity = {};
	// 搜索
	$scope.search = function(page,size){			
		contentService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}

	// 广告类型列表
	$scope.findContentCategoryList = function () {
		contentCategoryService.findContentCategoryList().success(function (resp) {
			if (resp) {
				$scope.contentCategoryList = resp;
			}else {
				alert("加载广告类型列表失败, 请稍后重试")
			}
		})
	}

	// 图片上传
	$scope.uploadFile = function () {
		uploadService.uploadFile().success(function (resp) {
			if (resp.success) {
				$scope.entity.pic = resp.message;
			} else {
				alert(resp.message);
			}
		})
	}

	// 状态
	$scope.statusName = ['未启用', '启用'];
    
});	
