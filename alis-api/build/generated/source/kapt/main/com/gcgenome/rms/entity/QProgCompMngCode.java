package com.gcgenome.rms.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProgCompMngCode is a Querydsl query type for ProgCompMngCode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProgCompMngCode extends EntityPathBase<ProgCompMngCode> {

    private static final long serialVersionUID = 960686371L;

    public static final QProgCompMngCode progCompMngCode = new QProgCompMngCode("progCompMngCode");

    public final StringPath compMngBeginNo = createString("compMngBeginNo");

    public final StringPath compMngCode = createString("compMngCode");

    public final StringPath compMngId = createString("compMngId");

    public final StringPath compMngName = createString("compMngName");

    public QProgCompMngCode(String variable) {
        super(ProgCompMngCode.class, forVariable(variable));
    }

    public QProgCompMngCode(Path<ProgCompMngCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProgCompMngCode(PathMetadata metadata) {
        super(ProgCompMngCode.class, metadata);
    }

}

