'use client';

import InputTextField from "@/app/(afterLogin)/request/services/_component/InputTextField";
import style from "@/app/(afterLogin)/request/services/precision-oncology/_component/inputExtension.module.css";
import {useEffect} from "react";
import {
    fetchServiceExtensions
} from "@/app/(afterLogin)/request/services/precision-oncology/_api/fetchServiceExtensions";
import {useSelectService} from "@/app/(afterLogin)/request/services/precision-oncology/store/useServiceStore";
import {
    useExtensions,
    useSetExtensions
} from "@/app/(afterLogin)/request/services/precision-oncology/store/useInputExtensionStore";
import CheckBox from "@/app/(afterLogin)/request/services/_component/CheckBox";
import TA0007Check from "@/app/(afterLogin)/request/services/precision-oncology/_component/TA0007Check";

interface Extension {
    id: string;
    name: string;
    regex: string;
    required: boolean;
}

export default function InputExtension() {
    const extensions = useExtensions();
    const setExtensions = useSetExtensions();
    const service = useSelectService();

    useEffect(() => {
        if (service) {
            fetchServiceExtensions(service?.id)
                .then((data: Extension[]) => {
                    setExtensions(data);
                });
        }
    }, [service]);

    const renderInput = (extension: Extension) => {
        if (extension.regex.startsWith('\\b(?:true|false)\\b')) {
            return <CheckBox key={extension.id} value={extension.id}/>
        } else if (extension.regex.startsWith('\\b(?:double|triple|quad)\\b')) {
            return <TA0007Check key={extension.id}/>
        } else {
            return <InputTextField key={extension.id} value={extension.id}/>;
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
                        {renderInput(extension)}
                    </div>
                ))}
            </div>
        );
    };

    const renderSections = () => {
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
                    {renderSections()}
                </div>
            )}
        </>
    );
}

