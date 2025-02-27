'use client';

import React from "react";
import style from './requestExtensionComponent.module.css';
import {Extension, ExtensionType} from "@/model/Extension";
import InputBox from "@/app/_component/InputBox";
import TextBox from "@/app/_component/TextBox";

interface ExtensionComponentProps {
    extensions: Extension[] | undefined
}

export default function RequestInfoExtensionComponent({ extensions }: ExtensionComponentProps) {
    const renderExtensionComponent = (extension: Extension) => {
        switch (extension.type) {
            case ExtensionType.LIST:
            case ExtensionType.BOOLEAN:
            case ExtensionType.INTEGER:
            case ExtensionType.FLOAT:
            case ExtensionType.STRING:
            case ExtensionType.RELATION:
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

    const textComponents = (extensions ?? []).filter(extension => extension.type === ExtensionType.TEXT);
    const otherComponents = (extensions ?? []).filter(extension => extension.type !== ExtensionType.TEXT);

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
        </div>
    );
}