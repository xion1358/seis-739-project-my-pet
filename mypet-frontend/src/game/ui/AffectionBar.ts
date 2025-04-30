export class AffectionBar {
    container: Phaser.GameObjects.Rectangle;
    fill: Phaser.GameObjects.Rectangle;
    text: Phaser.GameObjects.Text;
  
    constructor(scene: Phaser.Scene) {
      const barWidth = 234;
      const barHeight = 16;
      const padding = 20;

      const x = 800 - padding - (barWidth / 2);
      const y = padding + (barHeight / 2) + 32;

      this.container = scene.add.rectangle(x, y, barWidth, barHeight).setStrokeStyle(1, 0xffffff);
      this.fill = scene.add.rectangle(x - (barWidth / 2) + 2, y, 4, barHeight - 4, 0xffffff).setOrigin(0, 0.5);
      this.text = scene.add.text(x, y, 'Affection', { 
        fontSize: '12px', color: '#000000', fontFamily: 'Arial' 
      }).setOrigin(0.5);
    }
  
    updateAffectionBar(affection: number) {
      const fillWidth = 230;
      this.fill.width = (affection / 100) * fillWidth;
      this.fill.fillColor = affection >= 51 ? 0x00ff00 : affection >= 21 ? 0xffff00 : 0xff0000;
    }

    destroy()
    {
      
    }
  }
  