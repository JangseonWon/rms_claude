'use client';

import InputTextField from "@/app/(afterLogin)/request/services/_component/InputTextField";
import style from "@/app/(afterLogin)/request/services/precision-oncology/_component/inputExtension.module.css";
import {useEffect, useState} from "react";
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/precision-oncology/_api/fetchServiceExtensions";
import {useSelectService} from "@/app/(afterLogin)/request/services/precision-oncology/store/useServiceStore";

interface Extension {
    id: string;
    name: string;
    regex: string;
    required: boolean;
}

export default function InputExtension() {
    const [extensions, setExtensions] = useState<Extension[]>([]);
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
        if (extension.regex.startsWith('-?\\d+')) {
            return <InputTextField />;
        } else if (extension.regex.startsWith('\\b(?:true|false)\\b')) {
            return (
                <label form="agree" className={style.checkbox}>
                    <input type="checkbox" id="agree" />
                    <span className={style.on}></span>
                </label>
            );
        } else {
            return <InputTextField />;
        }
    };

    const renderExtensionSection = (sectionExtensions: Extension[]) => (
        <div className={style.section}>
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
                <>
                    <div className={style.mainName}>
                        Clinical Info.
                    </div>
                    {renderSections()}
                </>
            )}
        </>
    );
}

