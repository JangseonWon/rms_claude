'use client';

import React, {useEffect, useState} from "react";
import style from './extensionInputComponent.module.css';
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/_api/fetchServiceExtensions";
import {Extension, ExtensionType} from "@/model/Extension";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";
import {
    useProbandRequest,
    useSetProbandModalOpen, useSetProbandRequest
} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {useRequestStore} from "@/store/useRequestStore";

interface ExtensionInputComponentProps {
    serviceId: string;
}

export default function ExtensionInputComponent({serviceId}: ExtensionInputComponentProps) {
    const { request, setRequest } = useRequestStore();
    const setProbandModal = useSetProbandModalOpen();
    const [extensions, setExtensions] = useState<Extension[]>([]);
    const probandRequest = useProbandRequest();
    const setProbandRequest = useSetProbandRequest();
    const showAlert = CallAlertDialog();

    useEffect(() => {
        setProbandRequest(null);
    }, []);

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
                .replace(/^\\b|\b$/g, '')
                .replace(/^\(\?:/, '')
                .replace(/\)\\b$/, '')
                .replace(/\\b/g, '')
                .split('|')
                .map(value => value.trim())
                .filter(value => value !== '')
                .map(value => ({
                    name: value,
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
    const probandModalOpen = () => {
        if(request?.sample?.patient?.organization) setProbandModal(true);
        else showAlert("Please choose the institution")
    }

    const renderExtensionComponent = (extension: Extension) => {
        const selectList = generateSelectList(extension.regex || '');
        switch (extension.type) {
            case ExtensionType.LIST:
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
            case ExtensionType.PROBAND_LIST:
                return <SelectBox
                    key={'relationship'}
                    label={extension.name!}
                    options={selectList}
                    required={true}
                    width="200px"
                    onChange={(selectedOption) => {
                        setRequest((prevState) => ({
                            ...prevState,
                            request_relation: {
                                id: 3
                            }
                        }));
                        handleRequestChange("sample.extensions", {
                            id: extension.id,
                            value: selectedOption.name
                        })
                    }}
                />
            case ExtensionType.PROBAND_SEARCH:
                return <div className={style.probandInput}>
                    <InputBox
                        label={`Registration ID`}
                        required={true}
                        disabled={true}
                        value={probandRequest?.sample?.barcode}
                    />
                    <InputBox
                        label={`${extension.name}*`}
                        required={true}
                        disabled={true}
                        value={probandRequest?.sample?.patient?.serial}
                        onChange={(value) => {
                            setRequest((prevState) => ({
                                ...prevState,
                                request_relation: {
                                    id: 3
                                }
                            }));
                            handleRequestChange("sample.extensions", {
                                id: extension.id,
                                value: value
                            })
                        }}
                    />
                    <button className={style.button} onClick={probandModalOpen}>
                        Click here to find proband
                    </button>
                </div>
            default:
                return null;
        }
    };


    useEffect(() => {
        fetchExtensions();
    }, []);

    useEffect(() => {
        setRequest((prevState) => ({
            ...prevState,
            request_group: {
                id: probandRequest?.request_group?.id
            }
        }));
    }, [probandRequest]);

    return (
        <>
            <div className={style.section}>
                <div className={style.gridContainer}>
                    {extensions
                        .sort((a, b) => (a.sort_extension ?? 0) - (b.sort_extension ?? 0))
                        .map((extension) =>
                            extension.type === ExtensionType.TEXT ? (
                                <div key={extension.id}>
                                    {renderExtensionComponent(extension)}
                                </div>
                            ) : (extension.type === ExtensionType.PROBAND_SEARCH || extension.type === ExtensionType.PROBAND_LIST) ? null : (
                                <div key={extension.id} className={style.gridItem}>
                                    {renderExtensionComponent(extension)}
                                </div>
                            )
                        )}
                </div>
            </div>
            {extensions.some((ext) => (ext.type === ExtensionType.PROBAND_SEARCH || ext.type === ExtensionType.PROBAND_LIST)) && (
                <div>
                    <p className={style.title}>Proband Info.</p>
                    <div className={style.proband}>
                        {extensions
                            .filter((ext) => (ext.type === ExtensionType.PROBAND_SEARCH || ext.type === ExtensionType.PROBAND_LIST))
                            .sort((a, b) => (a.sort_extension ?? 0) - (b.sort_extension ?? 0))
                            .map((extension) => (
                                <div key={extension.id}>
                                    {renderExtensionComponent(extension)}
                                </div>
                            ))}
                    </div>
                </div>
            )}
        </>
    );
}