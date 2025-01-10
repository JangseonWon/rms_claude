'use client';

import React from "react";
import style from './extensionComponent.module.css';
import {Extension, ExtensionType} from "@/model/Extension";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";

interface ExtensionComponentProps {
    extensions: Extension[]
}

export default function RequestInfoExtensionComponent({ extensions }: ExtensionComponentProps) {

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
                    width="200px"
                />;
            case ExtensionType.INTEGER:
            case ExtensionType.FLOAT:
            case ExtensionType.STRING:
            case ExtensionType.PROBAND:
                return <InputBox
                    disabled={true}
                    key={extension.id}
                    label={extension.name}
                    value={extension.value}
                    required={extension.required}
                />;
            case ExtensionType.TEXT:
                return <TextBox
                    key={extension.id}
                    label={extension.name!}
                    value={extension.value}
                    required={extension.required}
                />;
            default:
                return null;
        }
    };

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
                <div>
                    {textComponents.map(extension => (
                        <div key={extension.id}>
                            {renderExtensionComponent(extension)}
                        </div>
                    ))}
                </div>
            )}
            {/*{probandComponent.length > 0 && (
                <ProbandComponent/>
            )}*/}
        </div>
    );
}