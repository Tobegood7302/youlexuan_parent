//seller服务层
app.service('sellerService', function($http){

	this.add = function (entity) {
		return $http.post('./seller/add.do', entity);
	}

});