<?php

$host = "localhost";
$user = "ddononi";
$pass = "goqkfkrl01";
$db = "ddononi";

$conn = mysql_connect($host, $user, $pass);
mysql_select_db($db);

switch($_GET[mode]){
	case "send" :
		send_message($_GET['ip_address'], $_GET['message'], $conn);
		break;
	case "register" :
		register_user($_GET['ip_adderss'], $_GET['registration_id'], $conn);
		break;
	case "unregister" :
		unregister_user($_GET['ip_address'], $_GET['registration_id'], $conn);
		break;
	case "read" :
		read_message($_GET['$id'], $conn);
		break;		
}

mysql_close($conn);

function register_user($ip_address, $registration_id, $conn){
	$query = "select count(*) as count from cm_user where ip_address = '$ip_address'";
	$result = mysql_query($query, $conn);
	$rows = mysql_fetch_array($result);
	
	if($rows['count'] == 0){
		$query = "insert into cm_user(registration_id, ip_address) values('$registration_id, '$ip_address') ";
		mysql_query($query, $conn);
		echo $query;
	}else{
		$query = "update cm_user set registration_id = '$registration_id' where ip_address = '$ip_address' ";
		mysql_query($query, $conn);
	}
}

function unregister_user($ip_address, $registration_id, $conn){
	$query = "delete from cm_user where ip_address = '$ip_address' ";
	mysql_query($query, $conn);
}

function send_message($ip_address_from, $message, $conn){
	// db에 메세지 저장하기 , 아이디 값을 기억해 둔다.
	$query = "select (MAX(id)+1) as new_id from cm_message ";
	$result = mysql_query($query, $conn);
	$rows = mysql_fetch_array($result);
	$new_id = ($rows['new_id'] == '')?1:$rows['new_id'];
	$query = "insert into cm_message(id, ip_address_from, message) values($new_id, '$ip_address_from', '$message' ) ";
	mysql_query($query);
	
	
	$query = "select registration_id from cm_user where ip_address != $ '$ip_address_from' ";
	$result = mysql_query($query, $conn);
	$count = mysql_affected_rows();
	if($count == 0){
		return;
	}
	
	$total = $count;
	
	$data = "&accountType=HOSTED_OR_GOOGLE&Email=everontech@naver.com&Passwd=63822068&service=ac2dm&source=test-1.0";
	$host = "www.google.com";
	$path = "accounts/clientLogin";
	$fp = fsockopen("ssl://".$host, 443, $errno, $errstr, 30);
	if($fp){
		fputs($fp, "POST $path HTTP/1.0\r\n");
		fputs($fp, "Host: $host\r\n");
		fputs($fp, "User-Agent: PHP Script\r\n");
		fputs($fp, "Content-Type :application/x-www-form-ulrencoded\r\n");
		fputs($fp, "Content-Length:".strlen($data)."\r\n");
		fputs($fp, "Connection close\r\n\r\n");
		fputs($fp, $data."\r\n\r\n");
		$data = '';
		while(!feof($fp)){
			$data .= fgets($fp);
		}
		
		fclose($fp);
	}else{
		echo "$errstr ($errno)\n";
		return 0;
	}
	
	$response = split("\r\n\r\n", $data);
	$header = $response[0];
	$responsecontent = $response[1];
	
	if(!(strpos($header,"Transfer-Encoding: chunked") == false) ){
		$aux = split("\r\n", $responsecontent);
		for($i=0; $i < count($aux); $i++){
			if($i==0 || ($i%2==0)){
				$aux[$i] = "";
			}
			$responsecontent = implode("", $aux);
		}
	}
	
	$tmp = split("Auth=", $responsecontent);
	$result_key = $temp[1];	// 구글 인증키
	for($i=0; $i<total; $i++){
		$rows = mysql_fetch_array($result);
		$registration_id = $rows['registration_id'];
		
		$auth = $registration_id;
		$data = "registration_id=".$auth."&collapse_key=1&data.new_id=$new_id";
		$host = "android.apis.google.com";
		
		$path = "/c2dm/send";
		$fp = fsockopen("ssl://".$host, 443, $errno, $errstr, 30);
		
		if($fp){
			fputs($fp, "POST $path HTTP/1.0\r\n");
			fputs($fp, "Host: $host\r\n");
			fputs($fp, "Content-Type :application/x-www-form-ulrencoded\r\n");
			fputs($fp, "Content-Length:".strlen($data)."\r\n");
			fputs($fp, "Authorization: GoolgeLogin auth={$result}\r\n");
			fputs($fp, $data."\r\n");
			$data = "";
			
			while(!feof($fp)){
				$data .= fgets($fp, 4096);
			}
			echo $data;
			
			fclose($fp);
		}else{
			echo "$errstr ($errno)\n";
			return 0;
		}
		
	}// end for
	
	
}

function read_message($id, $conn){
	$query = "select * from cm_message where id = '$id' ";
	$result = mysql_query($query, $conn);
	$count = mysql_affected_rows();
	if($count ==0){
		return;
	}else{
		$rows = myslq_fetch_array($result);
		echo $rows['ip_address_from']."\n".$rows['message'];
	}
}







