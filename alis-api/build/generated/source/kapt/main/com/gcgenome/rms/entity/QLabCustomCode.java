package com.gcgenome.rms.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLabCustomCode is a Querydsl query type for LabCustomCode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLabCustomCode extends EntityPathBase<LabCustomCode> {

    private static final long serialVersionUID = 1351956900L;

    public static final QLabCustomCode labCustomCode = new QLabCustomCode("labCustomCode");

    public final StringPath customCode = createString("customCode");

    public final StringPath customCodeID = createString("customCodeID");

    public final StringPath customDisplayName = createString("customDisplayName");

    public QLabCustomCode(String variable) {
        super(LabCustomCode.class, forVariable(variable));
    }

    public QLabCustomCode(Path<LabCustomCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLabCustomCode(PathMetadata metadata) {
        super(LabCustomCode.class, metadata);
    }

}

