package auxiliary;

public class EngineResponse<T> {
    T data;
    boolean success;

    public EngineResponse(T data, boolean success){
        this.data = data;
        this.success = success;
    }

    public boolean wasSuccessful(){
        return success;
    }

    public T getData(){return data;}
}
