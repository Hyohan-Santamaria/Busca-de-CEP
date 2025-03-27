public class ErroDeTipagemException extends RuntimeException {
    private  String mensagem;


    public ErroDeTipagemException(String s) {
        this.mensagem =  mensagem;

    }
    public String getMensagem(){
        return this.mensagem;
    }
}
