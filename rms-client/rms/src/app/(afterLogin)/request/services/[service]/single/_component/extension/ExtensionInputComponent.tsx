'use client';

import React, {useEffect, useState} from "react";
import style from './extensionInputComponent.module.css';
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/_api/fetchServiceExtensions";
import {Extension, ExtensionType} from "@/model/Extension";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {useProband, useRelationship} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";
import {ProbandComponent} from './ProbandComponenet';
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

interface ExtensionInputComponentProps {
    onChange: (path: string, value: any) => void;
    serviceId: string;
    onValidationChange?: (isValid: boolean) => void;
}

export default function ExtensionInputComponent({ serviceId, onChange, onValidationChange = () => {} }: ExtensionInputComponentProps) {
    const probandValue = useProband();
    const relationship = useRelationship();
    const [extensions, setExtensions] = useState<Extension[]>([]);
    const [values, setValues] = useState<{ [key: string]: any }>({});
    const [prevProbandValue, setPrevProbandValue] = useState(probandValue);
    const [prevRelationship, setPrevRelationship] = useState(relationship);
    const [validationState, setValidationState] = useState<{ [key: string]: boolean }>({});
    const showAlert = CallAlertDialog();

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

    const handleInputChange = (id: string, value: any, required: boolean) => {
        setValues(prevValues => ({ ...prevValues, [id]: value }));
        onChange(id, value);

        if (required) {
            setValidationState((prevState) => ({
                ...prevState,
                [id]: value !== undefined && value !== null && value !== ""
            }));
        }
    };

    const handleSelectChange = (id: string, option: SelectBoxOption, required: boolean) => {
        setValues(prevValues => ({ ...prevValues, [id]: option.name }));
        onChange(id, option.value);

        if (required) {
            setValidationState((prevState) => ({
                ...prevState,
                [id]: option.value !== undefined && option.value !== null && option.value !== ""
            }));
        }
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
                    label={extension.name!}
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
                    label={extension.name}
                    required={extension.required}
                    onChange={(inputValue) => handleInputChange(extension.id!, inputValue, extension.required!)}
                />;
            case ExtensionType.TEXT:
                return <TextBox
                    key={extension.id}
                    label={extension.name!}
                    onChange={(inputValue) => handleInputChange(extension.id!, inputValue, extension.required!)}
                />;
            default:
                return null;
        }
    };

    useEffect(() => {
        if (probandValue !== prevProbandValue) {
            handleInputChange('TEST01', probandValue, true);
            setPrevProbandValue(probandValue);
        }
    }, [probandValue, prevProbandValue]);

    useEffect(() => {
        if (relationship !== prevRelationship) {
            handleInputChange('TEST02', relationship, true);
            setPrevRelationship(relationship);
        }
    }, [relationship, prevRelationship]);

    useEffect(()=> {
        fetchExtensions();
    }, []);

    useEffect(() => {
        const allValid = Object.values(validationState).every((isValid) => isValid);
        onValidationChange(allValid);
    }, [validationState, onValidationChange]);

    const textComponents = extensions.filter(extension => extension.type === ExtensionType.TEXT);
    const otherComponents = extensions.filter(extension => extension.type !== ExtensionType.TEXT);
    const probandComponent = extensions.filter(extension => extension.type === ExtensionType.PROBAND);

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
            {probandComponent.length > 0 && (
                <ProbandComponent/>
            )}
        </div>
    );
}