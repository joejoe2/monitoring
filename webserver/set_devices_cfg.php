<?php
$user="root";
$pass="a43235167";
$DBname="data";
$table="devices_config";

$target=$_POST['target'];
$devicesid=$_POST['devicesid'];
$obj=$_POST['obj'];
$link=new MySQLi("localhost",$GLOBALS['user'],$GLOBALS['pass'],$DBname);
if($link->connect_error){ echo "error!";}

$s="SHOW TABLES LIKE '".$target."'";
$r=$link->query($s);
if (mysqli_num_rows($r)==1) {
    //echo "exist";
    $s="INSERT INTO "."$table"."(target,id,value) VALUES('"."$target"."','"."$devicesid"."','"."$obj"."')";
    $link->query($s);
}else{
    //echo "not exist";
    $s="CREATE TABLE ".$target." ( id VARCHAR(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL , status VARCHAR(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL , time VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL , value JSON NOT NULL ) ENGINE = InnoDB";
    $link->query($s);
    $s="INSERT INTO "."$table"."(target,id,value) VALUES('"."$target"."','"."$devicesid"."','"."$obj"."')";
    $link->query($s);
}

?>