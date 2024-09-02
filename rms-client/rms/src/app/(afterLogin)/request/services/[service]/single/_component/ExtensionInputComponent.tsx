'use client';

import {usePathname} from "next/navigation";
import {useEffect, useState} from "react";
import style from './extensionInputComponent.module.css';
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/_api/fetchServiceExtensions";
import {Extensions} from "@/model/ServiceExtensionAndSampleType";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";

export default function ExtensionInputComponent() {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);
    const [extensions, setExtensions] = useState<Extensions[]>([]);
    const selectList = [
        {name: "DOUBLE", value: 'double'},
        {name: "TRIPLE", value: 'triple'},
        {name: "QUAD", value: 'quad'},
    ]
    const booleanList = [
        {name: "TRUE", value: true},
        {name: "FALSE", value: false}
    ]

    const fetchExtensions = async () => {
        const response = await fetchServiceExtensions(serviceId);
        setExtensions(response as Extensions[]);
    };

    const renderExtensionComponent = (extension: Extensions) => {
        switch (extension.type) {
            case 'List':
                return <SelectBox
                    key={extension.id}
                    label={extension.name}
                    options={selectList}
                    required={extension.required}
                    width="11vw"
                />;
            case 'Boolean':
                return <SelectBox
                    key={extension.id}
                    label={extension.name}
                    options={booleanList}
                    required={extension.required}
                    width="11vw"
                />;
            case 'Int':
            case 'Number':
            case 'String':
                return <InputBox
                    key={extension.id}
                    label={extension.name}
                    required={extension.required}
                />;
            case 'Text':
                return <TextBox
                    key={extension.id}
                    label={extension.name}
                />;
            default:
                return null;
        }
    };

    useEffect(()=> {
        fetchExtensions();
    }, []);

    const textComponents = extensions.filter(extension => extension.type === 'Text');
    const otherComponents = extensions.filter(extension => extension.type !== 'Text');

    return (
        <div className={style.section}>
            {otherComponents.length > 0 && (
                <div className={style.gridContainer}>
                    {otherComponents.map((extension) => (
                        <div
                            key={extension.id}
                            className={style.gridItem}
                        >
                            {renderExtensionComponent(extension)}
                        </div>
                    ))}
                </div>
            )}
            {textComponents.length > 0 && (
                <div className={style.textContainer}>
                    {textComponents.map(extension => (
                        <div key={extension.id} className={style.singleItem}>
                            {renderExtensionComponent(extension)}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}