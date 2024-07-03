import Switch from "react-switch";
import {Component} from "react";
import style from '@/app/_component/switchButton.module.css';

interface SwitchButtonProps {
    id: string;
    checked: boolean;
    onToggle: (id: string, checked: boolean) => void;
}

class SwitchButton extends Component<SwitchButtonProps> {
    constructor(props: SwitchButtonProps) {
        super(props);
        this.handleChange = this.handleChange.bind(this);
    }

    handleChange(checked: boolean) {
        this.setState({ checked });
        this.props.onToggle(this.props.id, checked);
    }

    render() {
        return (
            <label className={style.switchContainer}>
                <Switch
                    className={style.switchContainer}
                    height={20}
                    width={40}
                    onChange={this.handleChange}
                    checked={this.props.checked}
                />
            </label>
        );
    }
}

export default SwitchButton;