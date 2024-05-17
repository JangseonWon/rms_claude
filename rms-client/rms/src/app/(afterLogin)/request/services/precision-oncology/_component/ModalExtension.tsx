'use client';

import style from "@/app/(afterLogin)/request/services/precision-oncology/_component/modalExtension.module.css";
import * as React from "react";
import {
    useExtensions,
    useSA0001,
    useTA0001,
    useTA0002,
    useTA0003,
    useTA0004,
    useTA0005,
    useTA0006,
    useTA0007,
    useTA0008,
    useTA0009,
    useTA0013,
    useTA0014,
    useTA0015,
    useTA0016,
    useTA0017,
    useTA0018,
    useTA0019,
    useTA0020,
    useTA0021,
    useTA0022,
    useTA0023,
    useTA0024,
    useTA0025,
    useTA0026,
    useTA0027,
    useTA0090,
    useTA0091,
    useTA0092,
    useTA0093,
    useTA0094,
    useTA0095
} from "@/app/(afterLogin)/request/services/precision-oncology/store/useInputExtensionStore";

interface Extension {
    id: string;
    name: string;
    regex: string;
    required: boolean;
}

export default function ModalExtension() {
    const extensions = useExtensions();
    const SA0001 = useSA0001();
    const TA0001 = useTA0001();
    const TA0002 = useTA0002();
    const TA0003 = useTA0003();
    const TA0004 = useTA0004();
    const TA0005 = useTA0005();
    const TA0006 = useTA0006();
    const TA0007 = useTA0007();
    const TA0008 = useTA0008();
    const TA0009 = useTA0009();
    const TA0013 = useTA0013();
    const TA0014 = useTA0014();
    const TA0015 = useTA0015();
    const TA0016 = useTA0016();
    const TA0017 = useTA0017();
    const TA0018 = useTA0018();
    const TA0019 = useTA0019();
    const TA0020 = useTA0020();
    const TA0021 = useTA0021();
    const TA0022 = useTA0022();
    const TA0023 = useTA0023();
    const TA0024 = useTA0024();
    const TA0025 = useTA0025();
    const TA0026 = useTA0026();
    const TA0027 = useTA0027();
    const TA0090 = useTA0090();
    const TA0091 = useTA0091();
    const TA0092 = useTA0092();
    const TA0093 = useTA0093();
    const TA0094 = useTA0094();
    const TA0095 = useTA0095();

    const renderExtensions = (extensionId: string) => {
        switch (extensionId) {
            case "SA0001": return SA0001;
            case "TA0001": return TA0001;
            case "TA0002": return TA0002;
            case "TA0003": return TA0003;
            case "TA0004": return TA0004;
            case "TA0005": return TA0005;
            case "TA0006": return TA0006 ? "true" : "false";
            case "TA0007": return TA0007;
            case "TA0008": return TA0008;
            case "TA0009": return TA0009;
            case "TA0013": return TA0013 ? "true" : "false";
            case "TA0014": return TA0014 ? "true" : "false";
            case "TA0015": return TA0015 ? "true" : "false";
            case "TA0016": return TA0016 ? "true" : "false";
            case "TA0017": return TA0017 ? "true" : "false";
            case "TA0018": return TA0018;
            case "TA0019": return TA0019;
            case "TA0020": return TA0020;
            case "TA0021": return TA0021;
            case "TA0022": return TA0022;
            case "TA0023": return TA0023;
            case "TA0024": return TA0024;
            case "TA0025": return TA0025;
            case "TA0026": return TA0026;
            case "TA0027": return TA0027;
            case "TA0090": return TA0090;
            case "TA0091": return TA0091;
            case "TA0092": return TA0092;
            case "TA0093": return TA0093;
            case "TA0094": return TA0094 ? "true" : "false";
            case "TA0095": return TA0095;
            default: return null;
        }
    };

    const renderExtensionSection = (sectionExtensions: Extension[]) => {
        return (
            <div key={sectionExtensions.map(extension => extension.id).join('-')} className={style.section}>
                {sectionExtensions.map((extension) => (
                    <div key={extension.id} className={style.item}>
                        <div className={style.name}>
                            {extension.name}
                        </div>
                        <div className={style.value}>
                            {renderExtensions(extension.id)}
                        </div>
                    </div>
                ))}
            </div>
        );
    };

    const renderExtensionSections = () => {
        const sections = [];
        for (let i = 0; i < extensions.length; i += 4) {
            const sectionExtensions = extensions.slice(i, i + 4);
            sections.push(renderExtensionSection(sectionExtensions));
        }
        return sections;
    };

    return (
        <>
            {extensions.length > 0 && (
                <div key="clinicalInfo">
                    <div className={style.mainName}>
                        Clinical Info.
                    </div>
                    {renderExtensionSections()}
                </div>
            )}
        </>
    );
}

