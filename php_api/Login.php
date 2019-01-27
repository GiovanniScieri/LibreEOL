<?php
require_once('/home/u949913003/public_html/includes/config.php');
require_once('/home/u949913003/public_html/includes/sqlDB.php');

$db = new sqlDB();

$response = array("error" => FALSE);

if (isset($_POST['email']) && isset($_POST['password'])) {
 
    $email = $_POST['email'];
    $password = sha1($_POST['password']);

    $user = $db->qLogin($email, $password);
    
    if ($user != NULL) {
        $response["error"] = FALSE;
        $response["id"] = $user["id"];
        $response["user"]["name"] = $user["name"];
        $response["user"]["surname"] = $user["surname"];
        $response["user"]["email"] = $user["email"];
        $response["user"]["role"] = $user["role"];
        echo json_encode($response);
    } else {
        $response["error"] = TRUE;
        $response["error_msg"] = "Login fallito";
        echo json_encode($response);
    }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Parametri mancanti";
    echo json_encode($response);
}

?>