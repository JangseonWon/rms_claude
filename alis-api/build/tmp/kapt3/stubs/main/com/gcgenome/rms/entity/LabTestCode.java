package com.gcgenome.rms.entity;

@jakarta.persistence.Entity()
@jakarta.persistence.Table(name = "LabTestCode", schema = "dbo")
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0010\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\tJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0007H\u00c6\u0003J;\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0015\u001a\u00020\u00072\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\nR\u0016\u0010\b\u001a\u00020\u00078\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\nR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\f\u00a8\u0006\u001a"}, d2 = {"Lcom/gcgenome/rms/entity/LabTestCode;", "", "testCodeId", "", "testCode", "testDisplayName", "isTestSub", "", "isTestUse", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZ)V", "()Z", "getTestCode", "()Ljava/lang/String;", "getTestCodeId", "getTestDisplayName", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "", "toString", "alis-api"})
public final class LabTestCode {
    @jakarta.persistence.Id()
    @jakarta.persistence.Column(name = "TestCodeID")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String testCodeId = null;
    @jakarta.persistence.Column(name = "TestCode")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String testCode = null;
    @jakarta.persistence.Column(name = "TestDisplayName")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String testDisplayName = null;
    @jakarta.persistence.Column(name = "IsTestSub")
    private final boolean isTestSub = false;
    @jakarta.persistence.Column(name = "IsTestUse")
    private final boolean isTestUse = false;
    
    public LabTestCode(@org.jetbrains.annotations.NotNull()
    java.lang.String testCodeId, @org.jetbrains.annotations.NotNull()
    java.lang.String testCode, @org.jetbrains.annotations.NotNull()
    java.lang.String testDisplayName, boolean isTestSub, boolean isTestUse) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTestCodeId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTestCode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getTestDisplayName() {
        return null;
    }
    
    public final boolean isTestSub() {
        return false;
    }
    
    public final boolean isTestUse() {
        return false;
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
    
    public final boolean component4() {
        return false;
    }
    
    public final boolean component5() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.gcgenome.rms.entity.LabTestCode copy(@org.jetbrains.annotations.NotNull()
    java.lang.String testCodeId, @org.jetbrains.annotations.NotNull()
    java.lang.String testCode, @org.jetbrains.annotations.NotNull()
    java.lang.String testDisplayName, boolean isTestSub, boolean isTestUse) {
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