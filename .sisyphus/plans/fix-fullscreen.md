# Fix Fullscreen & Performance

## Issues
1. System status bar shows white bar - need true fullscreen
2. TV performance lag - need analysis

## TODOs
- [x] 1. MainActivity - add FEATURE_NO_TITLE, setDecorFitsSystemWindows(false)
- [x] 2. Run performance analysis on TV device

## Performance Analysis Results
- Janky frames: 100% (应该<10%)
- Frame time: 2000ms (应该<16ms)
- Root causes:
  1. HeroBanner: 9个Ken Burns动画持续运行
  2. NetflixCard: 5个动画同时运行(scale+glow+elevation)
  3. 总共23个动画

## Recommendation
需要优化/禁用动画:
- HeroBanner: 减少Ken Burns频率或禁用
- NetflixCard: 减少动画或使用简化版本
