'use client';

import React, {useEffect, useState} from "react";
import style from './extensionInputComponent.module.css';
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/_api/fetchServiceExtensions";
import {Extension, ExtensionType} from "@/model/Extension";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";
import {useRequestStore} from '@/store/useRequestStore';
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

interface ExtensionInputComponentProps {
    serviceId: string;
}

export default function ExtensionInputComponent({ serviceId }: ExtensionInputComponentProps) {
    const { request, setRequest } = useRequestStore();
    const [extensions, setExtensions] = useState<Extension[]>([]);
    const showAlert = CallAlertDialog();

    const handleRequestChange = (path: string, value: any) => {
        setRequest((prevState) => ({
            ...prevState,
            ...setNestedValue({ ...prevState }, path, value)
        }));
    };

    const setNestedValue = (object: any, nestedPath: string, newValue: any): any => {
        const [firstKey, ...remainingPathSegments] = nestedPath.split('.');
        if (remainingPathSegments.length === 0) {
            if (Array.isArray(object[firstKey])) {
                const updatedArray = object[firstKey].some((item: any) => item.id === newValue.id)
                    ? object[firstKey].map((item: any) =>
                        item.id === newValue.id ? { ...item, ...newValue } : item
                    )
                    : [...object[firstKey], newValue];
                return { ...object, [firstKey]: updatedArray };
            }
            return { ...object, [firstKey]: newValue };
        }
        return {
            ...object,
            [firstKey]: setNestedValue(object[firstKey] || {}, remainingPathSegments.join('.'), newValue),
        };
    };

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
        if(response.ok){
            const data: Extension[] = await response.json()
            setExtensions(data);
            setRequest((prevState) => ({
                ...prevState,
                sample: {
                    ...prevState?.sample,
                    extensions: data
                }
            }));
        }else{
            showAlert("Error!")
        }
    };

    const renderExtensionComponent = (extension: Extension) => {
        switch (extension.type) {
            case ExtensionType.LIST:
                const selectList = generateSelectList(extension.regex || '');
                return <SelectBox
                    key={extension.id}
                    label={extension.name!}
                    options={selectList}
                    required={extension.required}
                    onChange={(selectedOption) => handleRequestChange("sample.extensions", { id: extension.id, value: selectedOption.value })}
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
                    options={booleanList}
                    required={extension.required}
                    onChange={(selectedOption) => handleRequestChange("sample.extensions", { id: extension.id, value: selectedOption.value })}
                    width="200px"
                />;
            case ExtensionType.INTEGER:
            case ExtensionType.FLOAT:
            case ExtensionType.STRING:
                return <InputBox
                    key={extension.id}
                    label={extension.name}
                    required={extension.required}
                    onChange={(value) => handleRequestChange("sample.extensions", { id: extension.id, value: value })}
                />;
            case ExtensionType.TEXT:
                return <TextBox
                    key={extension.id}
                    label={extension.name!}
                    onChange={(value) => handleRequestChange("sample.extensions", { id: extension.id, value: value })}
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