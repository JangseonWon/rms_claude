package com.gcgenome.rms.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProgCompCode is a Querydsl query type for ProgCompCode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProgCompCode extends EntityPathBase<ProgCompCode> {

    private static final long serialVersionUID = -216663875L;

    public static final QProgCompCode progCompCode = new QProgCompCode("progCompCode");

    public final StringPath compCode = createString("compCode");

    public final StringPath compCodeId = createString("compCodeId");

    public final StringPath compMngCode = createString("compMngCode");

    public final StringPath compName = createString("compName");

    public QProgCompCode(String variable) {
        super(ProgCompCode.class, forVariable(variable));
    }

    public QProgCompCode(Path<ProgCompCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProgCompCode(PathMetadata metadata) {
        super(ProgCompCode.class, metadata);
    }

}

