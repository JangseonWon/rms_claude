'use client';

import {usePathname} from "next/navigation";
import {useEffect, useState} from "react";
import style from './extensionInputComponent.module.css';
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/_api/fetchServiceExtensions";
import {Extensions} from "@/model/ServiceExtensionAndSampleType";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";

interface ExtensionInputComponentProps {
    onChange: (path: string, value: any) => void;
}

export default function ExtensionInputComponent({ onChange }: ExtensionInputComponentProps) {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);
    const [extensions, setExtensions] = useState<Extensions[]>([]);

    const generateSelectList = (regex: string): { name: string, value: string }[] => {
        if (regex.includes("|")) {
            return regex
                .replace(/\\b|\b/g, '')
                .replace(/\\|\(|\)|\?:/g, '')
                .split('|')
                .filter(value => value.trim() !== '')
                .map(value => ({
                    name: value.toUpperCase(),
                    value: value.toLowerCase()
                }));
        }
        return [];
    };

    const fetchExtensions = async () => {
        const response = await fetchServiceExtensions(serviceId);
        setExtensions(response as Extensions[]);
    };

    const handleChange = (id: string, value: any) => {
        onChange(id, value);
    };

    const renderExtensionComponent = (extension: Extensions) => {
        switch (extension.type) {
            case 'List':
                const selectList = generateSelectList(extension.regex);
                return <SelectBox
                    key={extension.id}
                    label={extension.name}
                    // value={}
                    options={selectList}
                    required={extension.required}
                    onChange={(value) => handleChange(extension.id, value)}
                    width="11vw"
                />;
            case 'Boolean':
                const booleanList = [
                    {name: "TRUE", value: true},
                    {name: "FALSE", value: false}
                ];
                return <SelectBox
                    key={extension.id}
                    label={extension.name}
                    // value={}
                    options={booleanList}
                    required={extension.required}
                    onChange={(value) => handleChange(extension.id, value)}
                    width="11vw"
                />;
            case 'Int':
            case 'Number':
            case 'String':
                return <InputBox
                    key={extension.id}
                    label={extension.name}
                    // value={}
                    required={extension.required}
                    onChange={(value) => handleChange(extension.id, value)}
                />;
            case 'Text':
                return <TextBox
                    key={extension.id}
                    // value={}
                    label={extension.name}
                    onChange={(value) => handleChange(extension.id, value)}
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