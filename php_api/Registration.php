<?php
require_once('/home/u949913003/public_html/includes/config.php');
require_once('/home/u949913003/public_html/includes/sqlDB.php');

$db = new sqlDB();

$response = array("error" => FALSE);


if (isset($_POST['name']) && isset($_POST['surname']) && isset($_POST['email']) && isset($_POST['password']) && isset($_POST['subgroup'])) {
	
	$name = $_POST['name'];
	$surname = $_POST['surname'];
	$email = $_POST['email'];
	$token = NULL;
	$role = NULL;
	$subgroup = $_POST['subgroup'];
	$password = sha1($_POST['password']); 

	$db->qListGroup();
	$groupList = $db->getResultAssoc();
	$i = 0;
	while($i != count($groupList)){
		if($groupList[$i]["NameSubGroup"] == $subgroup){
			$groupId = $groupList[$i]["idGroup"];
			$subgroupId = $groupList[$i]["idSubGroup"];
		}
		$i++;
	}
	
	$db->qSelect('Users', 'email', $email);
	$control = $db->nextRowAssoc();

	if($control == NULL){
		$user = $db->qNewUser($name, $surname, $email, $token, $role, $groupId, $subgroupId, $password);
	
		if ($user != false) {
			$response["error"] = FALSE;
			$response["user"] = "Utente creato con successo";
			echo json_encode($response);
		} else {
			$response["error"] = TRUE;
			$response["error_msg"] = "Registrazione fallita";
			echo json_encode($response);
		}
	}else{
		$response["error"] = TRUE;
		$response["error_msg"] = "Utente gia esistente";
		echo json_encode($response);
	}
} else {
	$response["error"] = TRUE;
	$response["error_msg"] = "Parametri mancanti";
	echo json_encode($response);
}

?>