package backend.productservice.enums;

public enum Estado {
    ACTIVO(true),
    INACTIVO(false);

    private final boolean valor;

    Estado(boolean valor) {
        this.valor = valor;
    }

    public boolean getValor() {
        return valor;
    }
}
