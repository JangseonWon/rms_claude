package com.gcgenome.rms.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLabTestCode is a Querydsl query type for LabTestCode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLabTestCode extends EntityPathBase<LabTestCode> {

    private static final long serialVersionUID = 1689283525L;

    public static final QLabTestCode labTestCode = new QLabTestCode("labTestCode");

    public final BooleanPath isTestSub = createBoolean("isTestSub");

    public final BooleanPath isTestUse = createBoolean("isTestUse");

    public final StringPath testCode = createString("testCode");

    public final StringPath testCodeId = createString("testCodeId");

    public final StringPath testDisplayName = createString("testDisplayName");

    public QLabTestCode(String variable) {
        super(LabTestCode.class, forVariable(variable));
    }

    public QLabTestCode(Path<LabTestCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLabTestCode(PathMetadata metadata) {
        super(LabTestCode.class, metadata);
    }

}

