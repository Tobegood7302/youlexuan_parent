//item_cat控制层 
app.controller('itemCatController' ,function($scope, $controller, itemCatService, typeTemplateService){
	
	// 继承
	$controller("baseController", {
		$scope : $scope
	});
	
	// 保存
	$scope.save = function() {
		itemCatService.save($scope.entity, $scope.parentId).success(function(response) {
			if (response.success) {
				// 重新加载
				$scope.findByParentId($scope.parentId);
			} else {
				alert(response.message);
			}
		});
	}
	
	//查询实体 
	$scope.findOne = function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//批量删除 
	$scope.dele = function(){			
		//获取选中的复选框			
		itemCatService.dele($scope.selectIds).success(
			function(response){
				if(response.success){
					if (response.message) {
						alert(response.message + "有下级没有删除")
					}
					$scope.findByParentId($scope.parentId);
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	// 定义搜索对象 
	$scope.searchEntity = {};
	// 搜索
	$scope.search = function(page,size){			
		itemCatService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;
			}			
		);
	}

	// 记录上级Id
	$scope.parentId = 0;
	// 根据上级id查询下级列表
	$scope.findByParentId = function (parentId) {

		// 记录上级Id
		$scope.parentId = parentId;

		itemCatService.findByParentId(parentId).success(function (resp) {
			$scope.list = resp;
		})
	}

	$scope.grade = 1;// 默认为1级
	// 设置级别
	$scope.setGrade = function (value) {
		$scope.grade = value;
	}

	// 更改上一级
	$scope.changeParent = function (p_entity) {
		if ($scope.grade == 1) {
			$scope.entity1 = null;
			$scope.entity2 = null;
		}

		if ($scope.grade == 2) {
			$scope.entity1 = p_entity;
			$scope.entity2 = null;
		}

		if ($scope.grade == 3) {
			$scope.entity2 = p_entity;
		}

		$scope.findByParentId(p_entity.id);
	}


	// 查询模板
	// $scope.typeIds = {data:[{'id':1,'text':'1'},{'id':2,'text':'2'}]};
	$scope.typeIds = {data:[]};
	$scope.selectTypeList = function () {
		typeTemplateService.selectTypeList().success(function (resp) {
			$scope.typeIds = {data:resp};
		})
	}
    
});	
