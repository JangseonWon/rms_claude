package com.gcgenome.rms.entity;

@jakarta.persistence.Entity()
@jakarta.persistence.Table(name = "ProgCompCode", schema = "dbo")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J1\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\u0018"}, d2 = {"Lcom/gcgenome/rms/entity/ProgCompCode;", "", "compCodeId", "", "compCode", "compName", "compMngCode", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getCompCode", "()Ljava/lang/String;", "getCompCodeId", "getCompMngCode", "getCompName", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "alis-api"})
public final class ProgCompCode {
    @jakarta.persistence.Id()
    @jakarta.persistence.Column(name = "CompCodeID")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String compCodeId = null;
    @jakarta.persistence.Column(name = "CompCode")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String compCode = null;
    @jakarta.persistence.Column(name = "CompName")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String compName = null;
    @jakarta.persistence.Column(name = "CompMngCode")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String compMngCode = null;
    
    public ProgCompCode(@org.jetbrains.annotations.NotNull()
    java.lang.String compCodeId, @org.jetbrains.annotations.NotNull()
    java.lang.String compCode, @org.jetbrains.annotations.NotNull()
    java.lang.String compName, @org.jetbrains.annotations.NotNull()
    java.lang.String compMngCode) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCompCodeId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCompCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCompName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getCompMngCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.gcgenome.rms.entity.ProgCompCode copy(@org.jetbrains.annotations.NotNull()
    java.lang.String compCodeId, @org.jetbrains.annotations.NotNull()
    java.lang.String compCode, @org.jetbrains.annotations.NotNull()
    java.lang.String compName, @org.jetbrains.annotations.NotNull()
    java.lang.String compMngCode) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}