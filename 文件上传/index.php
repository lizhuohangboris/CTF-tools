<?php
highlight_file(__FILE__);
function sup(string $name,int $pass){
    if($name=="sup"&$pass==114514){
        echo file_get_contents("/flag");
    }
}

function damn(string $name,int $pass){
    echo("godamn man,there's no fking flag");
}
class ctf{
    public $name = "sup";

    public function __construct(){
    }
    public function __wakeup(){
        $this->name = "damn";

    }
    public function __call($fun,$arg){
        if($fun=="damnshit"){
            if(preg_match("/^[a-z0-9,.\']+$/i", $arg[0])){
                if(strlen(explode(",",$arg[0])[0])>10){
                    $func = $this->name."(".$arg[0].");";
                    echo "damn man,you so crazy";
                    eval($func);
                }else{
                    die("gundamn");
                }
            }else{
                die("zhadamn");
            }
        }

    }

}

class export{
    public $clazz = "";
    public $args = "";
    public function __construct(){

    }
    public function __destruct(){
        $this->clazz->damnshit($this->args);
    }
}
if(isset($_POST['gangshit'])&&strstr($_POST['gangshit'],"\"export\":2")){
unserialize($_POST['gangshit']);}