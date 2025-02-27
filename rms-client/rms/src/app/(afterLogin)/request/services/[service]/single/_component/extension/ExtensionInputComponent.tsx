'use client';

import React, {useEffect, useState} from "react";
import style from './extensionInputComponent.module.css';
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/_api/fetchServiceExtensions";
import {Extension, ExtensionType} from "@/model/Extension";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";
import {useProbandRequest, useSetProbandModalOpen} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {useRequestStore} from "@/store/useRequestStore";
import {getRequestRelations} from "@/app/(afterLogin)/request/services/[service]/single/_api/getRequestRelations";
import {RequestRelation} from "@/model/RequestRelation";
import {SelectBoxOption} from "@/model/SelectBoxOption";

interface ExtensionInputComponentProps {
    serviceId: string;
}

export default function ExtensionInputComponent({serviceId}: ExtensionInputComponentProps) {
    const { request, setRequest } = useRequestStore();
    const setProbandModal = useSetProbandModalOpen();
    const [extensions, setExtensions] = useState<Extension[]>([]);
    const probandRequest = useProbandRequest()
    const [relationOptions, setRelationOptions] = useState<SelectBoxOption[]>([])
    const showAlert = CallAlertDialog();

    const handleRequestChange = (path: string, value: any) => {
        setRequest((prevState) => ({
            ...prevState,
            ...setNestedValue({...prevState}, path, value)
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
                .replace(/^\\b|\b$/g, '')
                .replace(/^\(\?:|\)$/g, '')
                .replace(/\\b/g, '')
                .split('|')
                .map(value => value.trim())
                .filter(value => value !== '')
                .map(value => ({
                    name: value.replace(/^\(|\)$/g, ''),
                    value: value.replace(/^\(|\)$/g, '').toLowerCase()
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
    const fetchRequestGroup = async () => {
        const response = await getRequestRelations()
        if (response.ok) {
            const data = await response.json();
            setRelationOptions(transformRequestRelationToOptions(data as RequestRelation[]));
        }
    }
    const transformRequestRelationToOptions = (data: RequestRelation[]): SelectBoxOption[] => {
        return data
            .filter(value => value.id !==1)
            .map(value => ({
                value: value.id,
                name: value.name
            }));
    };

    const probandModalOpen = () => {
        if(request?.sample?.patient?.organization) setProbandModal(true);
        else showAlert("Please choose the institution")
    }

    const renderExtensionComponent = (extension: Extension) => {
        switch (extension.type) {
            case ExtensionType.LIST:
                const selectList = generateSelectList(extension.regex || '');
                return <SelectBox
                    key={extension.id}
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
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
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
                    options={booleanList}
                    required={extension.required}
                    onChange={(selectedOption) => handleRequestChange("sample.extensions", { id: extension.id, value: selectedOption.value })}
                    width="200px"
                />;
            case ExtensionType.INTEGER:
            case ExtensionType.STRING:
                return <InputBox
                    key={extension.id}
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
                    required={extension.required}
                    regex = {extension.regex}
                    onChange={(value) => handleRequestChange("sample.extensions", { id: extension.id, value: value })}
                />;
            case ExtensionType.FLOAT:
                return <InputBox
                    key={extension.id}
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
                    required={extension.required}
                    regex = {"float"}
                    onChange={(value) => handleRequestChange("sample.extensions", { id: extension.id, value: value })}
                />;
            case ExtensionType.TEXT:
                return <TextBox
                    key={extension.id}
                    label={extension.name!}
                    required={extension.required}
                    onChange={(value) => handleRequestChange("sample.extensions", { id: extension.id, value: value })}
                />;
            case ExtensionType.RELATION:
                if(extension.id === 'TA0028'){
                    return <SelectBox
                        key={'relationship'}
                        label={extension.name!}
                        options={relationOptions}
                        required={true}
                        width="200px"
                        onChange={(selectedOption) => {
                            setRequest((prevState) => ({
                                ...prevState,
                                request_relation: {
                                    id: selectedOption.value
                                }
                            }));
                            handleRequestChange("sample.extensions", {
                                id: extension.id,
                                value: selectedOption.name
                            })
                        }}
                    />
                }else if(extension.id === 'TA0029') {
                    return <div className={style.probandInput}>
                        <InputBox
                            key={'probandInput'}
                            label={`${extension.name}*`}
                            required={true}
                            disabled={true}
                            value={probandRequest?.sample?.patient?.serial}
                            onChange={(value) => {
                                handleRequestChange("sample.extensions", {
                                    id: extension.id,
                                    value: value
                                })
                            }
                            }
                        />
                    </div>
                }
                return null
            default:
                return null;
        }
    };


    useEffect(() => {
        fetchExtensions();
        fetchRequestGroup()
    }, []);
    useEffect(() => {
        setRequest((prevState) => ({
            ...prevState,
            request_group: {
                id: probandRequest?.request_group?.id
            }
        }));
    }, [probandRequest]);

    const textComponents = extensions.filter(extension => extension.type === ExtensionType.TEXT);
    const excludedTypes = [ExtensionType.TEXT, ExtensionType.RELATION];
    const otherComponents = extensions.filter(extension => !excludedTypes.includes(extension.type!));
    const probandComponent = extensions.filter(extension => extension.type === ExtensionType.RELATION);

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
            {probandComponent.length > 0 && (
                <div>
                    <p className={style.title}>Proband Info.</p>
                    <div className={style.proband}>
                        {probandComponent.map((extension => (
                            <div key={extension.id}>
                                {renderExtensionComponent(extension)}
                            </div>
                        )))}
                        <button className={style.button} onClick={probandModalOpen}>Click here to find proband</button>
                    </div>
                </div>
            )}
        </>
    );
}