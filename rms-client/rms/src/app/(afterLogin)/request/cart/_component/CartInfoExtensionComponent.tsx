'use client';

import React, {useEffect} from "react";
import style from './cartExtensionComponent.module.css';
import {Extension, ExtensionType} from "@/model/Extension";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {ProbandComponent} from "@/app/(afterLogin)/request/cart/_component/ProbandComponent";
import SearchProbandModal
    from "@/app/(afterLogin)/request/services/[service]/single/_component/extension/SearchProbandModal";
import {
    useProband,
    useProbandModalOpen, useRelationship,
    useSetProbandModalOpen
} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";

interface ExtensionComponentProps {
    extensions: Extension[];
    onChange: (updatedExtensions: Extension[]) => void;
}

export default function CartInfoExtensionComponent({ extensions, onChange }: ExtensionComponentProps) {
    const probandModal = useProbandModalOpen();
    const probandValue = useProband();
    const relationship = useRelationship();
    const setProbandModal = useSetProbandModalOpen();

    const Close = () => {
        setProbandModal(false);
    }

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

    const updateExtensionValue = (id: string, value: any) => {
        const updatedExtensions = extensions.map(extension => {
            if (extension.id === id) {
                return { ...extension, value };
            }
            return extension;
        });
        onChange(updatedExtensions);
    };

    const handleInputChange = (id: string, value: any) => {
        updateExtensionValue(id, value);
    };

    const handleSelectChange = (id: string, option: SelectBoxOption) => {
        updateExtensionValue(id, option.value);
    };

    useEffect(() => {
        updateExtensionValue("TEST01", probandValue);
    }, [probandValue]);

    useEffect(() => {
        updateExtensionValue("TEST02", relationship);
    }, [relationship]);

    const renderExtensionComponent = (extension: Extension) => {
        switch (extension.type) {
            case ExtensionType.LIST:
                const selectList = generateSelectList(extension.regex || '');
                return <SelectBox
                    key={extension.id}
                    label={extension.name!}
                    value={extension.value}
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
                    value={extension.value}
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
                    value={extension.value}
                    required={extension.required}
                    onChange={(inputValue) => handleInputChange(extension.id!, inputValue)}
                />;
            case ExtensionType.TEXT:
                return <TextBox
                    key={extension.id}
                    label={extension.name!}
                    value={extension.value}
                    required={extension.required}
                    onChange={(inputValue) => handleInputChange(extension.id!, inputValue)}
                />;
            default:
                return null;
        }
    };

    const textComponents = extensions.filter(extension => extension.type === ExtensionType.TEXT);
    const otherComponents = extensions.filter(extension => extension.type !== ExtensionType.TEXT);
    const probandComponent = extensions.filter(extension => extension.type === ExtensionType.RELATION);

    return (
        <div className={style.section}>
            {otherComponents.length > 0 && (
                <div className={style.gridContainer}>
                    {otherComponents.map((extension) => {
                        const component = renderExtensionComponent(extension);
                        return component ? (
                            <div key={extension.id} className={style.gridItem}>
                                {component}
                            </div>
                        ) : null;
                    })}
                </div>
            )}
            {textComponents.length > 0 && (
                <div>
                    {textComponents.map((extension) => {
                        const component = renderExtensionComponent(extension);
                        return component ? (
                            <div key={extension.id}>
                                {component}
                            </div>
                        ) : null;
                    })}
                </div>
            )}
            {probandComponent.length > 0 && (
                <ProbandComponent extensions={probandComponent}/>
            )}
            {probandModal && (
                <SearchProbandModal closeModal={Close}/>
            ) }
        </div>
    );
}