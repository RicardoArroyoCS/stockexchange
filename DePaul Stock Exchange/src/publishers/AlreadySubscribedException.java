package publishers;

public class AlreadySubscribedException extends Exception{
	AlreadySubscribedException(String s){
		super(s);
	}
}