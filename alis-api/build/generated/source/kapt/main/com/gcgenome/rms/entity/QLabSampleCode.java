package com.gcgenome.rms.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLabSampleCode is a Querydsl query type for LabSampleCode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLabSampleCode extends EntityPathBase<LabSampleCode> {

    private static final long serialVersionUID = -1218158755L;

    public static final QLabSampleCode labSampleCode = new QLabSampleCode("labSampleCode");

    public final StringPath sampleCode = createString("sampleCode");

    public final StringPath sampleFullName = createString("sampleFullName");

    public final StringPath testCodeId = createString("testCodeId");

    public QLabSampleCode(String variable) {
        super(LabSampleCode.class, forVariable(variable));
    }

    public QLabSampleCode(Path<LabSampleCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLabSampleCode(PathMetadata metadata) {
        super(LabSampleCode.class, metadata);
    }

}

