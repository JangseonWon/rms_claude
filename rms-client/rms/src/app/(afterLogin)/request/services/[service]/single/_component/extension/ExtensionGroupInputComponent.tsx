'use client';

import React, {useEffect, useState} from "react";
import style from './extensionInputComponent.module.css';
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/_api/fetchServiceExtensions";
import {Extension, ExtensionType} from "@/model/Extension";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {SelectBoxOption} from "@/model/SelectBoxOption";

interface ExtensionGroupInputComponentProps {
    onChange: (id: string, value: any, required: boolean) => void;
    serviceId: string;
    onValidationChange?: (isValid: boolean) => void;
}

export default function ExtensionGroupInputComponent({ serviceId, onChange, onValidationChange = () => {} }: ExtensionGroupInputComponentProps) {
    const [extensions, setExtensions] = useState<Extension[]>([]);
    const [values, setValues] = useState<{ [key: string]: any }>({});
    const [validationState, setValidationState] = useState<{ [key: string]: boolean }>({});
    const showAlert = CallAlertDialog();

    useEffect(() => {
        if (extensions.length > 0) {
            const initialValues: { [key: string]: any } = {};
            extensions.forEach((extension) => {
                initialValues[extension.id!] = "";
                onChange(extension.id!, "", extension.required ?? false);
            });
            setValues(initialValues);
        }
    }, [extensions]);

    const handleInputChange = (id: string, value: any, required: boolean) => {
        setValues(prevValues => ({ ...prevValues, [id]: value }));
        onChange(id, value, required);

        if (required) {
            setValidationState((prevState) => ({
                ...prevState,
                [id]: value !== undefined && value !== null && value !== ""
            }));
        }
    };

    const handleSelectChange = (id: string, option: SelectBoxOption, required: boolean) => {
        setValues(prevValues => ({ ...prevValues, [id]: option.name }));
        onChange(id, option.value, required);

        if (required) {
            setValidationState((prevState) => ({
                ...prevState,
                [id]: option.value !== undefined && option.value !== null && option.value !== ""
            }));
        }
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
                .replace(/^\^\(\?:|\)\$$/g, '')
                .split("|")
                .map(v=> v.trim().replace(/\\([.*+?^${}()|\[\]\\])/g, '$1'))
                .map(v => ({name: v, value: v}))
        }
        return [];
    };

    const fetchExtensions = async () => {
        const response = await fetchServiceExtensions(serviceId);
        if(response.ok){
            const data: Extension[] = await response.json()
            setExtensions(data);
            const initialValidationState: { [key: string]: boolean } = {};
            data.forEach((extension: Extension) => {
                if (extension.required) {
                    initialValidationState[extension.id!] = false;
                }
            });
            setValidationState(initialValidationState);
        }else{
            showAlert("Error!")
        }
    };

    const renderExtensionComponent = (extension: Extension) => {
        let value = values[extension.id!] || '';

        switch (extension.type) {
            case ExtensionType.LIST:
                const selectList = generateSelectList(extension.regex || '');
                return <SelectBox
                    key={extension.id}
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
                    value={value}
                    options={selectList}
                    required={extension.required}
                    onChange={(selectedOption) => handleSelectChange(extension.id!, selectedOption, extension.required!)}
                    width="200px"
                />;
            case ExtensionType.BOOLEAN:
                const booleanList = [
                    {name: "TRUE", value: true},
                    {name: "FALSE", value: false}
                ];
                return <SelectBox
                    key={extension.id}
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
                    value={value}
                    options={booleanList}
                    required={extension.required}
                    onChange={(selectedOption) => handleSelectChange(extension.id!, selectedOption, extension.required!)}
                    width="200px"
                />;
            case ExtensionType.INTEGER:
            case ExtensionType.FLOAT:
            case ExtensionType.STRING:
                return <InputBox
                    key={extension.id}
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
                    value={value}
                    required={extension.required}
                    regex = {extension.regex}
                    onChange={(inputValue) => handleInputChange(extension.id!, inputValue, extension.required!)}
                />;
            case ExtensionType.TEXT:
                return <TextBox
                    key={extension.id}
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
                    value={value}
                    required={extension.required}
                    onChange={(inputValue) => handleInputChange(extension.id!, inputValue, extension.required!)}
                />;
            default:
                return null;
        }
    };


    useEffect(() => {
        fetchExtensions();
    }, []);

    useEffect(() => {
        const allValid = Object.values(validationState).every((isValid) => isValid);
        onValidationChange(allValid);
    }, [validationState, onValidationChange]);

    const textComponents = extensions.filter(extension => extension.type === ExtensionType.TEXT);
    const otherComponents = extensions.filter(extension => extension.type !== ExtensionType.TEXT);

    return (
        <>
            <div className={style.section}>
                {otherComponents.length > 0 && (
                    <div className={style.gridContainer}>
                        {otherComponents.map((extension) => (
                            <div key={extension.id} className={style.gridItem}>
                                {renderExtensionComponent(extension)}
                            </div>
                        ))}
                    </div>
                )}
                {textComponents.length > 0 && (
                    <div>
                        {textComponents.map(extension => (
                            <div key={extension.id}>
                                {renderExtensionComponent(extension)}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </>
    );
}