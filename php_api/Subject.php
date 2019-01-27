<?php
require_once('/home/u949913003/public_html/includes/config.php');
require_once('/home/u949913003/public_html/includes/sqlDB.php');

$db = new sqlDB();

$response = array("error" => FALSE);
$subject = array();
$readedSubjectsId = array();
$arrayAppoggio = array();
$readedSubjectsName = array();

if(isset($_POST['idUser'])){

	$idUser = $_POST['idUser'];

	$db->qSelect('Users', 'idUser', $idUser);
	$user = $db->nextRowAssoc();
	$subGroup = $user['subgroup'];

	if($db->qExamsInProgress($subGroup)){
		while($subject = $db->nextRowAssoc()){
			if(! in_array($subject['fkSubject'], $readedSubjectsId)){
				array_push($readedSubjectsId, $subject['fkSubject']);
			}
		}

		$i = 0;
		while($i != count($readedSubjectsId)){
			$db->qSelect('Subjects', 'idSubject', $readedSubjectsId[$i]);
			$arrayAppoggio = $db->nextRowAssoc();
			array_push($readedSubjectsName, $arrayAppoggio);
			$i++;
		}

		if($readedSubjectsName != NULL){
			$response["error"] = FALSE;
			$response["subject"]= $readedSubjectsName;
			echo json_encode($response);
		} else {
			$response["error"] = TRUE;
		$response["error_msg"] = "Materie non presenti";
		echo json_encode($response);
		}
	} else {
		$response["error"] = TRUE;
	$response["error_msg"] = "Query lista materie fallita";
	echo json_encode($response);
	}
} else {
	$response["error"] = TRUE;
	$response["error_msg"] = "Id non ricevuto";
	echo json_encode($response);
}

?>