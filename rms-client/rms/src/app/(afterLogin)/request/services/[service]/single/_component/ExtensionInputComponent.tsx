'use client';

import {usePathname} from "next/navigation";
import {useEffect, useState} from "react";
import style from './extensionInputComponent.module.css';
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/_api/fetchServiceExtensions";
import {Extension, ExtensionType} from "@/model/Extension";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";

interface ExtensionInputComponentProps {
    onChange: (path: string, value: any) => void;
}

export default function ExtensionInputComponent({ onChange }: ExtensionInputComponentProps) {
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);
    const [extensions, setExtensions] = useState<Extension[]>([]);
    const [values, setValues] = useState<{ [key: string]: any }>({});

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
        setExtensions(response as Extension[]);
    };

    const handleInputChange = (id: string, value: any) => {
        setValues(prevValues => ({ ...prevValues, [id]: value }));
        onChange(id, value);
    };

    const handleSelectChange = (id: string, option: SelectBoxOption) => {
        setValues(prevValues => ({ ...prevValues, [id]: option.name }));
        onChange(id, option.value);
    };

    const renderExtensionComponent = (extension: Extension) => {
        let value = values[extension.id!] || '';

        switch (extension.type) {
            case ExtensionType.LIST:
                const selectList = generateSelectList(extension.regex || '');
                return <SelectBox
                    key={extension.id}
                    label={extension.name!}
                    value={value}
                    options={selectList}
                    required={extension.required}
                    onChange={(selectedOption) => handleSelectChange(extension.id!, selectedOption)}
                    width="200px"
                />;
            case ExtensionType.BOOLEAN:
                const booleanList = [
                    {name: "TRUE", value: true},
                    {name: "FALSE", value: false}
                ];
                return <SelectBox
                    key={extension.id}
                    label={extension.name!}
                    value={value}
                    options={booleanList}
                    required={extension.required}
                    onChange={(selectedOption) => handleSelectChange(extension.id!, selectedOption)}
                    width="200px"
                />;
            case ExtensionType.INTEGER:
            case ExtensionType.FLOAT:
            case ExtensionType.STRING:
                return <InputBox
                    key={extension.id}
                    label={extension.name}
                    required={extension.required}
                    onChange={(inputValue) => handleInputChange(extension.id!, inputValue)}
                />;
            case ExtensionType.TEXT:
                return <TextBox
                    key={extension.id}
                    label={extension.name!}
                    onChange={(inputValue) => handleInputChange(extension.id!, inputValue)}
                />;
            default:
                return null;
        }
    };

    useEffect(()=> {
        fetchExtensions();
    }, []);

    const textComponents = extensions.filter(extension => extension.type === ExtensionType.TEXT);
    const otherComponents = extensions.filter(extension => extension.type !== ExtensionType.TEXT);

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
                        <div key={extension.id}>
                            {renderExtensionComponent(extension)}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}